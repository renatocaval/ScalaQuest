package de.scala_quest.controller.defaultImpl

import de.scala_quest.{GameState, UpdateAction}
import de.scala_quest.controller.{Controller => ControllerTrait}
import de.scala_quest.model.{Player => PlayerTrait}
import de.scala_quest.model.defaultImpl.{Game, Player}

import scala.util.Random

case class Controller(private var gameState: GameState) extends ControllerTrait {

  // TODO remove ALL get's

  override def onQuit(): Unit = {
    gameState = GameState(UpdateAction.CLOSE_APPLICATION, gameState.game)
    notifyObservers(gameState)
  }

  override def newGame(): Unit = {
    //gameState = GameState(UpdateAction.NEW_GAME, gameState.game.createQuestionList)
    gameState = GameState(UpdateAction.DO_NOTHING, gameState.game.createQuestionList)
    notifyObservers(gameState)
  }

  override def startGame(): Unit = {
    //gameState = GameState(UpdateAction.NEW_GAME, gameState.game.createQuestionList) // TODO delete really
    gameState = GameState(UpdateAction.SHOW_GAME, gameState.game.start) // TODO delete
    notifyObservers(gameState)
  }

  override def addNewPlayerToGame(name: String): Unit = {
    val questionList = gameState.game.questionList

    // shuffle the questionList for each new player
    val newPlayer = Player(name, 0, 0, Random.shuffle(questionList), List(), List(), Option.empty)

    gameState = GameState(UpdateAction.PLAYER_UPDATE, gameState.game.addNewPlayer(newPlayer))
    notifyObservers(gameState)
  }

  /** Remove a player with the given name from the game.
   *
   * @param name the player's name
   */
  override def removePlayer(name: String): Unit = {
    val newPlayer = Player(name, 0, 0, List(), List(), List(), Option.empty)
    gameState = GameState(UpdateAction.PLAYER_UPDATE, gameState.game.removePlayer(newPlayer))
    notifyObservers(gameState)
  }

  override def getPlayerInfo() : (String, String) = {
    (gameState.game.currentPlayer.get.name, gameState.game.currentPlayer.get.points.toString)
  }

  def getCurrentPlayer(): Option[PlayerTrait] = gameState.game.currentPlayer

  override def getPlayerNames(): List[String] = gameState.game.players.map(player => player.name)

  override def getPlayersCurrentQuestion(): Option[String] = {
    if (gameState.game.currentPlayer.get.questionIndex >= gameState.game.currentPlayer.get.questions.length) {
      None
    } else {
      val game = gameState.game.nextQuestion(gameState.game.currentPlayer.get)
      gameState = GameState(UpdateAction.SHOW_GAME, game)
      Some(gameState.game.currentPlayer.get.currentQuestion.get.text)
    }
  }

  override def getPlayersCurrentAnswers(): List[String] = {
    // TODO remove gets
    gameState.game.currentPlayer.get.currentQuestion.get.answers.map(a => a.text)
  }

  override def getPlayerCount(): Int = gameState.game.playerCount()

  override def checkGameRoundStatus(): Boolean = {
    if (gameState.game.currentRoundNr <= gameState.game.maxRoundNr) {
      true
    } else {
      gameState = GameState(UpdateAction.SHOW_RESULT, gameState.game)
      notifyObservers(gameState)
      false
    }
  }

  /**
   * NB: Only used by the TUI. The GUI will need a different mechanism to process Answers.
   * @param input
   */
  def processAnswer(input: Int): Unit = {
    val player = getCurrentPlayer()
    val currentQuestion = player.get.questions.lift(player.get.questionIndex).get
    val correctAnswer = currentQuestion.correctAnswer

    val updatedPlayer = if(input == correctAnswer) {
      player.get.correctAnswer(currentQuestion)
    } else {
      player.get.wrongAnswer(currentQuestion)
    }
    val game = gameState.game.updatePlayer(updatedPlayer).updateState()
    if (gameState.game.currentRoundNr == gameState.game.maxRoundNr) {
      gameState = GameState(UpdateAction.SHOW_RESULT, game) // TODO delete
    } else {
      gameState = GameState(UpdateAction.SHOW_GAME, game) // TODO delete
    }
    //gameState = GameState(UpdateAction.DO_NOTHING, game) // changed from show game to do nothing
    notifyObservers(gameState)
  }

  override def getPlayers(): List[PlayerTrait] = gameState.game.players
  override def getRoundNr(): Int = gameState.game.currentRoundNr

  override def nextPlayerName(): Option[String] = {
    gameState.game.playerCount() match {
      case c if c < gameState.game.maxPlayerCount => Some("Player " + (gameState.game.playerCount + 1).toString)
      case _ => None
    }
  }

}
