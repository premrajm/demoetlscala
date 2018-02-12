package com.raft.core

class StubStateRepository() extends StateRepository {
  this.serverState = serverState
  private[raft] var serverState = new PersistentState

  override def get: PersistentState = serverState

  override def update(state: PersistentState): PersistentState = serverState
}
