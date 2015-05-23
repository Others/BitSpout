package spout.inject

trait RemoteListener[T]{
    def getMessage():(T, Seq[Byte])
}