package spout.client

import scala.collection.{concurrent, mutable}
import scala.concurrent.Channel

import spout.inject.{RemoteCommand, PacketManager, HashManager}
import spout.client.thread._

class BitSpoutClient[T](input:Iterator[String], command:RemoteCommand[T], generator:PacketManager, hashManagers:Map[Byte, HashManager]){
    
    //Data structures
        //A Map of (HASH_TYPE, FILE_HASH) to file
            //A file has the following information
                //Size
                //Related packets received
                //Decrypted blocks
                //Set of peers
                //Set of peers who have recently sent data
                //Set of peers who we should send data to
    val files=mutable.Map[(Byte, Seq[Byte]), TargetFile[T]]()
        //A queue of packets to be processed
    val packets=new Channel[Seq[Byte]]()

    //Five threads
        //One thread is the user thread
    val userThread=new UserThread(input, files, command, generator, hashManagers)
        //One thread is the receiver thread
    val reciverThread=new ReciverThread(packets, files, command, generator, hashManagers)
        //One thread is the watcher thread
    val watcherThread=new WatchingThread(files, command)
        //One thread is the transmitter thread
        //One thread is a decrypter tread
}