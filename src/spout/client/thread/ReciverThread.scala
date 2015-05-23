package spout.client.thread

import scala.collection.mutable
import scala.concurrent.Channel

import spout.inject.Constants._
import spout.client.TargetFile
import spout.inject.RemoteCommand
import spout.inject.HashManager
import spout.inject.PacketManager


class ReciverThread[T](packets:Channel[Seq[Byte]], files:mutable.Map[(Byte, Seq[Byte]), TargetFile[T]], controller:RemoteCommand[T], packetGen:PacketManager, hashManagers:Map[Byte, HashManager]) extends Runnable{
    def run()={
        while(true){
            val (polled, from)=controller.pollPeers()
            hashManagers.get(polled(1)).foreach{hashManager => 
                val code=polled(0)
                val hashType=polled(1)
                val stripedData=polled.tail
                val (hash, data)=hashManager.stripTypeAndHash(stripedData)
                files.get((hashType, hash)).fold[Unit]({
                    controller.transmitToPeer(from, packetGen.getPacketHandler(hashType, hash).generateStopSignal())
                })(file => {
                    code match{
                        case(SEND_DATA) => {
                            file.targetPeers+=from
                        }
                        case(STOP_DATA) => {
                            file.targetPeers-=from
                        }
                        case(DATA) => {
                            if(file.packetHandler.canDecrypt()){
                                controller.transmitToPeer(from, file.packetHandler.generateStopSignal())
                            }else{
                                file.packetHandler.addPacket(stripedData)
                            }
                        }
                        case(_) => 
                    }
                })
            }
        }
    }
}