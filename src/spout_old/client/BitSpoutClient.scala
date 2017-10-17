package spout_old.client

import java.util.Timer

import scala.collection.{concurrent, mutable}
import scala.concurrent.Channel

import spout_old.inject.{RemoteCommand, PacketManager, HashManager}
import spout_old.client.thread._

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
    val files=concurrent.TrieMap[(Byte, Seq[Byte]), TargetFile[T]]()
        //A queue of packets to be processed
    val packets=new Channel[Seq[Byte]]()
        //A timer
    val timer=new Timer();
    
    //Five threads
        //One thread is the user thread
    val userThread=new UserThread(input, files, command, generator, hashManagers)
    new Thread(userThread).start();
        //One thread is the receiver thread
    val reciverThread=new ReciverThread(packets, files, command, generator, hashManagers)
    new Thread(reciverThread).start();
        //One thread is a decrypter tread
    val decrypterThread=new DecrypterThread(packets, files, hashManagers)
    new Thread(decrypterThread).start();
        //One thread is the transmitter thread
    val transmitterThread=new TransmitterThread(files, command, generator)
    new Thread(transmitterThread).start();
        //One thread is the watcher thread
    val watcherThread=new WatchingThread(files, command, generator)
    timer.scheduleAtFixedRate(watcherThread, 0, 2*60*1000)


}