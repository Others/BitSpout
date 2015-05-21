package spout.inject

object Constants {
    //Infrastructure
    val SERVER_PORT:Char=53735
    val CLIENT_PORT:Char=53736
    
    //Server Message Codes
    val BEGIN_CONTRACTING:Byte=0
    val END_CONTRACTING:Byte=1
    val KEEP_ALIVE:Byte=2
    
    //Client Message Codes
    val SEND_DATA:Byte=0
    val STOP_DATA:Byte=1
    val DATA:Byte=2
    
    //Constraints
    val PEER_GOAL=50
}