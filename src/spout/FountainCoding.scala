package spout

/**
  * Created by peachg on 10/2/16
  */

case class Metadata(dataLength: Int)

case class Packet(parts: Set[Int], data: Array[Byte]){
    def reduce(newPacket: Packet): Packet = {
        // If the parts
        if(p.parts.subsetOf(this.parts)){}
    }
}

class Encoder(data: Array[Byte]){
    def getMetadata(): Metadata = {
        ???
    }
}

class Decoder(){
}