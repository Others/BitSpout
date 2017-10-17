package spout_old.client.thread

import scala.collection.mutable
import scala.io.Source

import java.nio.file.{FileSystems, Files}

import spout_old.client.TargetFile
import spout_old.inject.RemoteCommand
import spout_old.inject.PacketManager
import spout_old.inject.HashManager
import spout_old.inject.Constants._

class UserThread[T](commands:Iterator[String], files:mutable.Map[(Byte, Seq[Byte]), TargetFile[T]], controller:RemoteCommand[T], packetManager:PacketManager, hashManagers:Map[Byte, HashManager]) extends Runnable{
    def run()={
        for (command <- commands){
            val commandName :: arguments = command.split(" ").toList
            commandName match{
                case("CANCEL_FILE") => {
                    val hashType=arguments(0).toByte
                    val fileHash=arguments(1).split("[,]").map(_.toByte)
                    files.get((hashType, fileHash)).foreach(_.server.endConnection())
                    files.remove((hashType, fileHash))
                }
                case("GET_FILE") => {
                    val server=arguments(0)
                    val hashType=arguments(1).toByte
                    val hash=arguments(2).split("[,]").map(_.toByte)
                    val fileByteCount=arguments(3).toLong
                    files.put((hashType, hash), new TargetFile(fileByteCount, server, hashType, hash, controller, Left(packetManager)))
                }
                case("HOST_FILE") => {
                    val server=arguments(0)
                    val hashType=arguments(1).toByte
                    val path=FileSystems.getDefault().getPath(arguments(2))
                    val bytes=Files.readAllBytes(path)
                    val hash=hashManagers.get(hashType).get.hash(bytes)
                    val packet=packetManager.getPacketHandler(hashType, hash, path)
                    files.put((hashType, hash), new TargetFile(bytes.length, server, hashType, hash, controller, Right(packet)))
                }
            }
        }
    }
}