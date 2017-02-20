package com.cbtsoft.pokercenter.goldenflower.model;

import com.cbtsoft.pokercenter.core.model.Card;
import com.cbtsoft.pokercenter.core.model.Dealer;
import com.cbtsoft.pokercenter.core.model.Deck;
import com.cbtsoft.pokercenter.core.model.StandardDeck;
import com.cbtsoft.pokercenter.core.pojo.Action;
import com.cbtsoft.pokercenter.core.pojo.Message;
import com.cbtsoft.pokercenter.core.pojo.Player;
import com.cbtsoft.pokercenter.goldenflower.helper.GoldenFlowerWrapperComparator;
import com.cbtsoft.pokercenter.goldenflower.wrapper.GoldenFlowerCardListWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GoldenFlowerDealer extends Dealer {
    private static final Logger logger = LoggerFactory.getLogger(GoldenFlowerDealer.class);

    protected Map<Integer, Player> tableSnapshot;
    protected Map<Player, Integer> tableSnapshotReverse;
    protected Map<Player, Integer> timedOutPlayerList;
    protected Deck deck;
    protected Map<Player, List<Card>> playerCards;
    protected List<Card> cardsOnBoard;
    protected Map<Player, Long> currentTotalBet;
    protected Map<Player, Long> currentTurnBet;
    protected long currentTurnMaxBet;
    protected int currentPos;
    protected GameStatus gameStatus;
    protected List<Player> playerBlacklist;
    // 本轮按钮位
    protected int button = 0;

    protected Timer gameBeginCountDownTimer = new Timer();
    protected Timer userTimeoutCountDownTimer = new Timer();

    private static final int REST_TIME_IN_SECONDS = 3;

    private static final int MAX_PLAYER_NUM = 8;

    private static final long BLIND_VALUE = 10;
    @Override
    public void sit(Player player) {
        // If dealer is waiting for players then check whether he can start this game or not.
        if (status == Status.WAITING) {
            if (room.getTable().size() >= 2) {
                startBeginCountdown();
            }
            if (room.getTable().size() > MAX_PLAYER_NUM) {
                throw new UnsupportedOperationException("Current player number(" + room.getTable().size() + ") is greater than MAX_PLAYER_NUM.");
            }
        }
        // If dealer is serving a game then ignore the new player.
    }

    @Override
    public void stand(Player player) {
        Map<Integer, Player> currentTable = room.getTable();
        if (status == Status.WAITING) {
            if (currentTable.size() < 2) {
                stopBeginCountDown();
            }
        } else if (status == Status.SERVING) {
            if (tableSnapshot.values().contains(player) && !playerBlacklist.contains(player)) {
                playerBlacklist.add(player);
            }
        }
    }

    @Override
    public void statusChanged(Player player) {
        // ignore
    }

    @Override
    public synchronized void handleAction(Action action) {
        Player player = action.getPlayer();
        switch (ActionType.getTypeByValue(action.getActionType())) {
            case CALL:
                if (tableSnapshotReverse.get(player) != currentPos) {
                    room.sendMessage(player, new Message("It's not your turn."));
                    return;
                }
                long callValue = Long.valueOf(action.getDetail());
                if (callValue > player.getChips()) {
                    room.sendMessage(player, new Message("Do not have enough chip."));
                    return;
                }
                long currentTurnValue = callValue + currentTurnBet.getOrDefault(player, 0L);
                if (currentTurnValue < currentTurnMaxBet) {
                    if (player.getChips() > callValue)  {
                        room.sendMessage(player, new Message("Illegal call."));
                    } else {
                        deduct(player, callValue);
                        currentTurnBet.put(player, currentTurnValue);
                        currentTotalBet.put(player, currentTotalBet.getOrDefault(player, 0L) + callValue);
                        room.sendMessage(new Message(
                                "Player Number " + currentPos + "(" + player.getUserName() + ") call to " + currentTurnValue + ". All in!"));
                        nextPlayer();
                    }
                    return;
                } else if (currentTurnValue == currentTurnMaxBet) {
                    deduct(player, callValue);
                    currentTurnBet.put(player, currentTurnValue);
                    currentTotalBet.put(player, currentTotalBet.getOrDefault(player, 0L) + callValue);
                    if (currentTurnMaxBet > 0) {
                        room.sendMessage(new Message(
                                "Player Number " + currentPos + "(" + player.getUserName() + ") call to " + currentTurnValue));
                    } else {
                        room.sendMessage(new Message(
                                "Player Number " + currentPos + "(" + player.getUserName() + ") check"));
                    }
                    nextPlayer();
                    return;
                } else if (currentTurnValue > currentTurnMaxBet) {
                    if (currentTurnValue > maxBetValue()) {
                        room.sendMessage(player, new Message("Your bet is too large."));
                        return;
                    }
                    deduct(player, callValue);
                    currentTurnBet.put(player, currentTurnValue);
                    currentTotalBet.put(player, currentTotalBet.getOrDefault(player, 0L) + callValue);
                    room.sendMessage(new Message(
                            "Player Number " + currentPos + "(" + player.getUserName() + ") raised to " + currentTurnValue));
                    currentTurnMaxBet = currentTurnValue;
                    nextPlayer();
                    return;
                }
                return;
            case FOLD:
                if (tableSnapshotReverse.get(player) != currentPos) {
                    room.sendMessage(player, new Message("It's not your turn."));
                    return;
                }
                // fall through
            case LEAVE:
                room.sendMessage(new Message("Player Number " + tableSnapshotReverse.get(player) + "(" + player.getUserName() + ") flopped"));
                playerBlacklist.add(player);
                nextPlayer();
        }
    }

    private void nextPlayer() {
        if (tableSnapshot.size() - playerBlacklist.size() == 1) {
            gameOver();
            return;
        }
        currentPos = getNextPos(currentPos);
        if (currentTurnBet.getOrDefault(tableSnapshot.get(currentPos), -1L) == currentTurnMaxBet) {
            nextTurn();
            return;
        }
        room.sendMessage(tableSnapshot.get(currentPos), new Message("========It's your turn========"));
    }

    private void nextTurn() {
        if (gameStatus == GameStatus.RIVER_ROUND) {
            gameOver();
            return;
        }
        currentTurnMaxBet = 0L;
        currentTurnBet = new HashMap<>(tableSnapshot.size());
        switch (gameStatus) {
            case PRE_FLOP_ROUND:
                cardsOnBoard.add(deck.deal());
                room.sendMessage(new Message("Flop card: " + cardsOnBoard.get(0)));
                gameStatus = GameStatus.FLOP_ROUND;
                break;
            case FLOP_ROUND:
                cardsOnBoard.add(deck.deal());
                room.sendMessage(new Message("Turn card: " + cardsOnBoard.get(1)));
                gameStatus = GameStatus.TURN_ROUND;
                break;
            case TURN_ROUND:
                cardsOnBoard.add(deck.deal());
                room.sendMessage(new Message("River card: " + cardsOnBoard.get(2)));
                cardsOnBoard.add(deck.deal());
                gameStatus = GameStatus.RIVER_ROUND;
                break;
        }
        currentPos = button;
        nextPlayer();
    }

    private void gameOverWithOneSurvivor() {
        int survivorPos = getNextPos(button);
        Player survivor = tableSnapshot.get(survivorPos);
        long winChips = 0;
        for (Long v : currentTotalBet.values()) {
            winChips += v;
        }
        room.sendMessage(new Message("Player Number " + survivorPos + "(" + survivor.getUserName() + ") win!"));
        survivor.add(winChips);

        finish();
    }

    private void gameOver() {
        int firstOne = getNextPos(button);
        int currentPos = firstOne;
        List<GoldenFlowerCardListWrapper> playerList = new ArrayList<>();
        do {
            Player player = tableSnapshot.get(currentPos);
            playerList.add(new GoldenFlowerCardListWrapper(playerCards.get(player), player));
        } while ((currentPos = getNextPos(currentPos)) != firstOne);
        Collections.sort(playerList, new GoldenFlowerWrapperComparator(cardsOnBoard));
        Player winner = playerList.get(0).getPlayer();
        int winnerPos = tableSnapshotReverse.get(winner);

        long winChips = 0;
        for (Long v : currentTotalBet.values()) {
            winChips += v;
        }
        room.sendMessage(new Message("Player Number " + winnerPos + "(" + winner.getUserName() + ") win!"));
        addChip(winner, winChips);

        finish();
    }

    private void finish() {
        if (room.getTable().size() >= 2) {
            startBeginCountdown();
        }
        status = Status.WAITING;
        room.end();
    }

    private long maxBetValue() {
        return 1000000;
        /*
        int maxChip = -1;
        for (Player player : tableSnapshot.values()) {
            if (floppedPlayer.contains(player)) {
                continue;
            }
        }
        */
    }

    private int determineButton(int lastButton) {
        for (int i = 1; i < MAX_PLAYER_NUM; i++) {
            int key = (i - 1) % MAX_PLAYER_NUM + 1;
            if (tableSnapshot.containsKey(key)) {
                room.sendMessage(new Message("Player Number " + i + "(" + tableSnapshot.get(key).getUserName() + ") is the button."));
                return i;
            }
        }
        throw new NullPointerException("Can't determine current button position.");
    }

    private void deduct(Player player, long v) {
        player.deduct(v);
        room.sendMessage(player, new Message("Deduct " + v + " chips. You have " + player.getChips() + " chips left."));
    }

    private void addChip(Player player, long v) {
        player.add(v);
        room.sendMessage(player, new Message("Get " + v + " chips. You have " + player.getChips() + " chips left."));
    }

    private void startBeginCountdown() {
        gameBeginCountDownTimer.scheduleAtFixedRate(new GameBeginCountDownTimerTask(REST_TIME_IN_SECONDS), 0, 1000);
    }

    private void stopBeginCountDown() {
        gameBeginCountDownTimer.cancel();
    }

    private void beginGame() {
        room.begin();
        status = Status.SERVING;
        try {
            // waiting for users who has just entered this room
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }

        tableSnapshot = room.getTable();
        playerBlacklist = new ArrayList<>(MAX_PLAYER_NUM);
        timedOutPlayerList = new HashMap<>(MAX_PLAYER_NUM);
        tableSnapshotReverse = new HashMap<>(tableSnapshot.size());
        playerCards = new HashMap<>(tableSnapshot.size());
        cardsOnBoard = new ArrayList<>(2);
        currentTotalBet = new HashMap<>(tableSnapshot.size());
        currentTurnBet = new HashMap<>(tableSnapshot.size());

        for (Map.Entry<Integer, Player> entry : tableSnapshot.entrySet()) {
            tableSnapshotReverse.put(entry.getValue(), entry.getKey());
        }
        deck = new StandardDeck();
        for (Player player : tableSnapshot.values()) {
            dealCardToPlayer(player);
        }
        gameStatus = GameStatus.PRE_FLOP_ROUND;
        button = determineButton(button);
        deductBlind();

        room.sendMessage(tableSnapshot.get(currentPos), new Message("========It's your turn======="));
    }

    private int getNextPos(int p) {
        for (int i = p + 1; i < p + MAX_PLAYER_NUM; i++) {
            int key = (i - 1) % MAX_PLAYER_NUM + 1;
            if (tableSnapshot.containsKey(key) && !playerBlacklist.contains(tableSnapshot.get(key))) {
                return key;
            }
        }
        throw new NullPointerException("Can't determine current button position.");
    }

    private void deductBlind() {
        int nextPos = getNextPos(button);
        Player player = tableSnapshot.get(nextPos);
        deduct(player, BLIND_VALUE);
        currentTurnBet.put(player, BLIND_VALUE);
        currentTotalBet.put(player, BLIND_VALUE);
        room.sendMessage(new Message("Player Number " + nextPos + "(" + player.getUserName() + ") raised to " + BLIND_VALUE));

        nextPos = getNextPos(nextPos);
        player = tableSnapshot.get(nextPos);
        deduct(player, BLIND_VALUE * 2);
        currentTurnBet.put(player, BLIND_VALUE * 2);
        currentTotalBet.put(player, BLIND_VALUE * 2);
        room.sendMessage(new Message("Player Number " + nextPos + "(" + player.getUserName() + ") raised to " + (BLIND_VALUE * 2)));

        currentTurnMaxBet = BLIND_VALUE * 2;
        currentPos = getNextPos(nextPos);
    }

    private void dealCardToPlayer(Player player) {
        List<Card> tempCards = deck.deal(2);
        playerCards.put(player, tempCards);
        room.sendMessage(player, new Message("You got a " + tempCards.get(0) + " and a " + tempCards.get(1)));
    }

    private class GameBeginCountDownTimerTask extends TimerTask {
        private int secondsLeft;

        public GameBeginCountDownTimerTask(int seconds) {
            secondsLeft = seconds;
        }

        @Override
        public void run() {
            if (secondsLeft-- == 0) {
                this.cancel();
                beginGame();
            } else {
                room.sendMessage(new Message("Game will begin in " + secondsLeft + " second(s)."));
            }
        }
    }

    public enum ActionType {
        TIMEOUT(1),
        FOLD(10),
        CALL(11),
        LEAVE(20);

        private int v;

        ActionType(int v) {
            this.v = v;
        }

        public int getValue() {
            return v;
        }

        public static ActionType getTypeByValue(int v) {
            for (ActionType actionType : ActionType.values()) {
                if (actionType.getValue() == v) {
                    return actionType;
                }
            }
            throw new IllegalArgumentException("Invalid action value. v=" + v);
        }
    }

    public enum GameStatus {
        PRE_FLOP_ROUND,
        FLOP_ROUND,
        TURN_ROUND,
        RIVER_ROUND,
    }
}
