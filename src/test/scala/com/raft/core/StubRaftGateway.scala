package com.raft.core

import java.util

class StubRaftGateway(val voteResponse: VoteResponse = null) extends RaftGateway {
  private val appendEntryRequest: AppendEntry = null
  private var voteRequest: RequestVote = null
  private[raft] val appendEntryMap: util.Map[Long, AppendEntry] = new util.HashMap[Long, AppendEntry]


  def getAppendEntryRequest: AppendEntry = appendEntryRequest

  def getVoteRequest: RequestVote = voteRequest

  override def requestVote(serverId: Long, voteRequest: RequestVote): VoteResponse = {
    this.voteRequest = voteRequest
    voteResponse
  }

  override def appendEntries(serverId: Long, appendEntry: AppendEntry): AppendEntryResponse = {
    appendEntryMap.put(serverId, appendEntry)
    new AppendEntryResponse
  }
}
