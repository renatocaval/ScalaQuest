package de.scala_quest.controller

import de.scala_quest.model.{Player, Question}

trait Controller {

  /** Starts a new game. */
  def newGame()

  /** Initializes the game. */
  def startGame()

  /** Quits the game. */
  def onQuit()

  /** Adds a new player with the given name to the game.
   *
   * @param name the player's name
   */
  def addNewPlayerToGame(name: String)

  /** Returns a list containing all player names.
   *
   * @return a list containing a string representation of the player's names
   */
  def getPlayerNames(): List[String]

  /** Returns a tuple
   *
   * @return
   */
  def getPlayerInfo(): (String, String)

  /** Returns the players current question.
   *
   * @return the question to be asked
   */
  def getPlayersCurrentQuestion(): Option[String]

  /** Returns the players current question answers.
   *
   * @return the answers to the current question
   */
  def getPlayersCurrentAnswers(): List[String]

  /** Gets the current game's round number.
   *
   * @return the current round number
   */
  def getRoundNr(): Int

  /** Returns a list of all players.
   *
   * @return the list of players
   */
  def getPlayers(): List[Player]

  /** Processes the keyboard input received from the user while answering a question.
   *
   * @param input a numerical representation of the keyboard input corresponding to the selected answer
   */
  def processAnswer(input: Int): Unit

}
