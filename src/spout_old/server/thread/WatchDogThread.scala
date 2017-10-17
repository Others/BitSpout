package spout_old.server.thread

import scala.collection.mutable
import scala.collection.concurrent

import java.util.TimerTask

class WatchDogThread[T](hashes:concurrent.TrieMap[(Byte,Seq[Byte]), mutable.Set[T]], feeders:mutable.Set[((Byte, Seq[Byte]), T)]) extends TimerTask{
    def run()={
        feeders.synchronized{
            for((hash, client) <- feeders){
                hashes.get(hash).foreach(_ -= client)
            }
            val clients=hashes.iterator.flatMap(x=>x._2.map((x._1, _)))
            feeders ++= clients
        }
    }
}