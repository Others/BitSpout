package spout.server.thread

import scala.collection.concurrent
import scala.collection.mutable

import spout.inject.Constants
import spout.inject.HashManager
import spout.inject.RemoteListener

class ControllerThread[T](r:RemoteListener[T], hashes:concurrent.TrieMap[(Byte,Seq[Byte]), mutable.Set[T]], feeders:mutable.Set[((Byte, Seq[Byte]), T)], hashManagers:Map[Byte, HashManager]) extends Runnable{
    def run()={
        while(true){
            val (client, bytes)=r.getMessage()
            val header :: hash = bytes.toSeq
            header match{
                case(Constants.BEGIN_CONTRACTING) =>{
                    val targetSet=hashes.getOrElseUpdate((hash(0), hash.tail), mutable.Set())
                    targetSet.add(client)
                }
                case(Constants.END_CONTRACTING) =>{
                    val targetSet=hashes.getOrElse((hash(0), hash.tail), mutable.Set())
                    targetSet -= client
                }
                case(Constants.KEEP_ALIVE) =>{
                    feeders synchronized{
                        feeders -=(((hash(0), hash.tail), client))
                    }
                }
            }
        }
    }
}