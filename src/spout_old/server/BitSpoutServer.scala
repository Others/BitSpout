package spout_old.server

import spout_old.inject.HashManager
import spout_old.inject.RemoteListener
import spout_old.server.thread.WatchDogThread
import spout_old.server.thread.ControllerThread
import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import java.util.Timer

class BitSpoutServer[T](r:RemoteListener[T], hashManagers:Map[Byte, HashManager]){
    //Data structures
        //Concurrent map of (HASH_TYPE, FILE_HASH) to List of IPs
    val hashes=new TrieMap[(Byte, Seq[Byte]), mutable.Set[T]]()
        //List of those who haven't fed the watch dog recently
    val feeders=mutable.Set[((Byte, Seq[Byte]), T)]()
    
    
    //Two threads
        //One is the controller thread, it listens for commands from clients
            //Command: BEGIN_CONTRACTING:(HASH_TYPE, FILE_HASH)
                //Response: OTHER_CONTRACTORS:(*OTHER_CONTRACTOR)
            //Command: END_CONTRACTING:(HASH_TYPE, FILE_HASH)
            //Command: KEEP_ALIVE
    new Thread(new ControllerThread(r, hashes, feeders, hashManagers)).start()
        //The other is the watch-dog thread
            //Every 10 minutes: remove all clients who haven't sent a KEEP_ALIVE since last watch dog
    new Timer().scheduleAtFixedRate(new WatchDogThread(hashes, feeders), 0, 10*60*1000)
}