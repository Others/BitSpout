package spout_old.inject

trait HashManager {
    def hashNumber:Byte
  
    def stripHash(bytes:Seq[Byte]):(Seq[Byte], Seq[Byte])
    
    def stripTypeAndHash(bytes:Seq[Byte]):(Seq[Byte], Seq[Byte])={
        stripHash(bytes.tail)
    }
    
    def hash(data:Seq[Byte]):Seq[Byte]
    
    def verifyHash(data:Seq[Byte], knownHash:Seq[Byte]):Boolean=(hash(data)==knownHash)
}