package com.cbtsoft.pokercenter.goldenflower.model;

import com.cbtsoft.pokercenter.core.model.Card;
import com.cbtsoft.pokercenter.core.model.Dealer;
import com.cbtsoft.pokercenter.core.model.Deck;
import com.cbtsoft.pokercenter.core.model.StandardDeck;
import com.cbtsoft.pokercenter.core.pojo.Action;
import com.cbtsoft.pokercenter.core.pojo.Message;
import com.cbtsoft.pokercenter.core.pojo.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class GoldenFlowerDealer extends Dealer {
    private static final Logger logger = LoggerFactory.getLogger(GoldenFlowerDealer.class);

    protected Map<Integer, Player> tableSnapshot;
    protected Map<Player, Integer> tableSnapshotReverse;
    protected List<Player> playerBlacklist;
    protected Map<Player, Integer> timedOutPlayerList;
    protected Deck deck;
    protected Map<Player, Collection<Card>> playerCards;
    protected List<Card> cardsOnBoard;
    protected Map<Player, Long> currentBet;
    protected long currentMaxBet;
    protected int currentPos;
    protected GameStatus gameStatus;
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

    public synchronized void handleAction(Action action) {
        Player player = action.getPlayer();
        switch (ActionType.getTypeByValue(action.getActionType())) {
            case CALL :
                long callValue = Long.valueOf(action.getDetail());
                if (callValue + currentBet.getOrDefault(player, 0L) < currentMaxBet) {
                    room.sendMessage(player, new Message("Illegal call."));
                }
        }
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
        currentBet = new HashMap<>(tableSnapshot.size());

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
            if (tableSnapshot.get((i - 1) % MAX_PLAYER_NUM + 1) != null) {
                return (i - 1) % MAX_PLAYER_NUM + 1;
            }
        }
        throw new NullPointerException("Can't determine current button position.");
    }

    private void deductBlind() {
        int nextPos = getNextPos(button);
        Player player = tableSnapshot.get(nextPos);
        player.deduct(BLIND_VALUE);
        currentBet.put(player, BLIND_VALUE);
        room.sendMessage(new Message("Player Number " + nextPos + "(" + player.getUserName() + ") raise to " + BLIND_VALUE));

        nextPos = getNextPos(nextPos);
        player = tableSnapshot.get(nextPos);
        player.deduct(BLIND_VALUE * 2);
        currentBet.put(player, BLIND_VALUE * 2);
        room.sendMessage(new Message("Player Number " + nextPos + "(" + player.getUserName() + ") raise to " + (BLIND_VALUE * 2)));

        currentMaxBet = BLIND_VALUE * 2;
        currentPos = getNextPos(nextPos);
    }

    private void dealFlopCards() {
        cardsOnBoard.add(deck.deal());
        cardsOnBoard.add(deck.deal());
        cardsOnBoard.add(deck.deal());
        room.sendMessage(new Message("Flop cards:" + cardsOnBoard.get(0) + ", " + cardsOnBoard.get(1) + ", " + cardsOnBoard.get(2)));
    }

    private void dealCardToPlayer(Player player) {
        List<Card> tempCards = new ArrayList<>(2);
        tempCards.add(deck.deal());
        tempCards.add(deck.deal());
        playerCards.put(player, tempCards);
        room.sendMessage(player, new Message("you got a " + tempCards.get(0) + " and a " + tempCards.get(1)));
    }

    private class GameBeginCountDownTimerTask extends TimerTask {
        private int secondsLeft;

        public GameBeginCountDownTimerTask(int seconds) {
            secondsLeft = seconds;
        }

        @Override
        public void run() {
            room.sendMessage(new Message("Game will begin in " + secondsLeft + " second(s)."));
            if (secondsLeft-- == 0) {
                this.cancel();
                beginGame();
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
