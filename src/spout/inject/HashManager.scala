package spout.inject

trait HashManager {
    def stripHash(bytes:Seq[Byte]):(Seq[Byte], Seq[Byte])
    
    def stripTypeAndHash(bytes:Seq[Byte]):(Seq[Byte], Seq[Byte])={
        stripHash(bytes.tail)
    }
    
    def hash(data:Seq[Byte]):Seq[Byte]
    
    def verifyHash(data:Seq[Byte], knownHash:Seq[Byte]):Boolean=(hash(data)==knownHash)
}