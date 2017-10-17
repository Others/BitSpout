package spout_old.impl

import scala.collection.mutable
import scala.collection.generic.CanBuildFrom
import scala.io.Source

final case class Packet(bytes:Seq[Byte]) extends Seq[Byte]{
    
    def iterator: Iterator[Byte] = bytes.iterator
  
    def apply(idx: Int): Byte = bytes(idx)
    
    def length: Int = bytes.length
    
    def mergeInt(o:Packet, op:(Byte, Byte) => Int):Packet=mergeByte(o, op(_, _).toByte)
    
    def mergeByte(o:Packet, op:(Byte, Byte) => Byte):Packet={
        if(o.length!=this.length) 
            throw new IllegalArgumentException()
        new Packet(this.zip(o).map{case(a,b) => op(a, b)})
    }
    
    def splitSafely(desiredSize:Int):(List[Packet], Int)={
        val additionalBytes=(desiredSize - (bytes.size % desiredSize)) % desiredSize
        (new Packet(bytes ++ List.fill[Byte](additionalBytes)(0)).splitExactly(desiredSize), additionalBytes)
    }
    
    def splitExactly(desiredSize:Int):List[Packet]={
        if(bytes.size % desiredSize != 0){
            throw new IllegalArgumentException();
        }
        if(bytes.size == desiredSize){
            new Packet(this.bytes)  :: Nil
        }else {
            val (packet, remainder)=bytes.splitAt(desiredSize)
            new Packet(packet) :: new Packet(remainder).splitExactly(desiredSize)
        }
    }
}

object packetCanBuildFrom extends CanBuildFrom[Seq[Byte], Byte, Packet]{
    final class PacketBuilder extends mutable.Builder[Byte, Packet]{
        var currentBytes:Seq[Byte]=Seq.empty
        def +=(elem: Byte):this.type = {
            currentBytes=currentBytes :+ elem
            this
        }
        def clear(): Unit = {
            currentBytes = Seq.empty
        }
        
        def result(): Packet = new Packet(currentBytes)
    }
    
    def apply(): mutable.Builder[Byte, Packet] = new PacketBuilder()
    def apply(from: Seq[Byte]): mutable.Builder[Byte, Packet] = new PacketBuilder()
}

object Packet{
      implicit def canBuildFrom: CanBuildFrom[Seq[Byte], Byte, Packet] = packetCanBuildFrom
}