package spout_old.impl

import spout_old.inject.HashManager

class FletcherHash extends HashManager{

    def hash(data: Seq[Byte]): Seq[Byte] = {
        var sum1=0
        var sum2=0
        for(byte <- data){
            sum1=(sum1+byte) % 255
            sum2=(sum2+sum1) % 255
        }
        sum1.toByte :: sum2.toByte :: Nil
    }
            
    def hashNumber: Byte = 0xF
    
    def stripHash(bytes: Seq[Byte]): (Seq[Byte], Seq[Byte]) = bytes.splitAt(2)
}