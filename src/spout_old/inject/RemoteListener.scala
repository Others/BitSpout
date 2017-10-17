package spout_old.inject

trait RemoteListener[T]{
    def getMessage():(T, Seq[Byte], (Seq[T] => _))
}