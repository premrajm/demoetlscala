package com.raft.core

import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

class RaftServerTest extends FunSuite with Matchers with BeforeAndAfterAll {


  test("should Initialize Term at startup") {
    val raftServer = new RaftServerImpl(0, List(1, 2), new StubStateRepository(), new StubRaftGateway())
    assert(raftServer.term == 0)
  }

  test("should start in follower state") {
    val raftServer = new RaftServerImpl(0, List(1, 2), new StubStateRepository(), new StubRaftGateway())
    assert(raftServer.role == ServerRole.Follower)
  }

  test("should Elect Itself If Single Server") {
    val server = new RaftServerImpl(0, List(), new StubStateRepository, new StubRaftGateway)
    server.startElection()
    assert(server.role == ServerRole.Leader)
    assert(1 == server.term)
  }

  test("should Become Leader If Gets Majority") {
    val server2 = new StubRaftGateway(VoteResponse.granted(1))
    val server1 = new RaftServerImpl(0, List(2), new StubStateRepository, server2)
    server1.startElection()
    assert(server1.role == ServerRole.Leader)
  }

  test("should send AppendEntry To Neighbours On Election") {
    val raftGateway = new StubRaftGateway(new VoteResponse(0, true))
    val server1 = new RaftServerImpl(0, List(1, 2), new StubStateRepository, raftGateway)
    server1.startElection()
    val appendEntryRequestFromLeader = raftGateway.appendEntryMap.get(2L)
    val appendEntryRequestFromLeader1 = raftGateway.appendEntryMap.get(1L)
    assert(0 == appendEntryRequestFromLeader.getLeader)
    assert(0 == appendEntryRequestFromLeader1.getLeader)
    assert(server1.term == appendEntryRequestFromLeader.getTerm)
    assert(List() == appendEntryRequestFromLeader.getEntries)
  }

  test("candidate Should Convert To Follower If AppendEntries Received From Leader") {
    val leader = new RaftServerImpl(0, List(1, 2), new StubStateRepository, new StubRaftGateway)
    val candidate = new RaftServerImpl(1, List(0, 2), new StubStateRepository, new StubRaftGateway)
    val appendEntry = new AppendEntry(2, 1, 0, 0, List(new LogEntry), 0)
    candidate.appendEntries(appendEntry)
    assert(candidate.role == ServerRole.Follower)
  }
}
