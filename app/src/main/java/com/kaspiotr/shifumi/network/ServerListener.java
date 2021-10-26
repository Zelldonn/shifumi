package com.kaspiotr.shifumi.network;

import com.kaspiotr.GameType;
import com.kaspiotr.Move;

public interface ServerListener {
	void onLobbiesList(ServerConnection connection, String ___TEMP___);
	void onGameCreated(ServerConnection connection, String gameID);
	void onNewPlayer(ServerConnection connection, String playerName);
	void onGameStart(ServerConnection connection, GameType type);
	void onGameEnd(ServerConnection connection, String winner);
	void onRoundStart(ServerConnection connection, long duration);
	void onRoundEnd(ServerConnection connection, boolean isDraw, String winner, Move othersMove);
	void onError(ServerConnection connection, String errorMessage);
	void onConnectionClosed(ServerConnection connection);
	void onJoined(ServerConnection connection);
}
