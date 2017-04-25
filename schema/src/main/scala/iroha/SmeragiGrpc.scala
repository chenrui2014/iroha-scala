package iroha

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.ByteBuffer

import io.grpc.MethodDescriptor.Marshaller
import org.apache.commons.io.IOUtils

/**
  * Created by daisuke-shimada on 2017/04/24.
  */
object SmeragiGrpc {

  val byteArrayMarshaller = new Marshaller[Array[Byte]] {
    override def parse(stream: InputStream): Array[Byte] = {
      IOUtils.toByteArray(stream)
    }

    override def stream(bytes: Array[Byte]): InputStream = {
      new ByteArrayInputStream(bytes)
    }
  }

  val responseMarshaller = new Marshaller[Response] {
    override def parse(stream: InputStream): Response = {
      val bytes = IOUtils.toByteArray(stream)
      Response.getRootAsResponse(ByteBuffer.wrap(bytes))
    }
    override def stream(value: Response): InputStream = {
      new ByteArrayInputStream(value.messageAsByteBuffer().array())
    }
  }

  val METHOD_TORII: _root_.io.grpc.MethodDescriptor[Array[Byte], Response] =
    _root_.io.grpc.MethodDescriptor.create(
      _root_.io.grpc.MethodDescriptor.MethodType.UNARY,
      _root_.io.grpc.MethodDescriptor.generateFullMethodName("iroha.Sumeragi", "Torii"),
      byteArrayMarshaller,
      responseMarshaller)

  val METHOD_VERIFY: _root_.io.grpc.MethodDescriptor[Array[Byte], Response] =
    _root_.io.grpc.MethodDescriptor.create(
      _root_.io.grpc.MethodDescriptor.MethodType.UNARY,
      _root_.io.grpc.MethodDescriptor.generateFullMethodName("iroha.Sumeragi", "Verify"),
      byteArrayMarshaller,
      responseMarshaller)

  class MyBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) {
    def torii(request: Array[Byte]): Response = {
      _root_.io.grpc.stub.ClientCalls.blockingUnaryCall(channel, METHOD_TORII, options, request)
    }
  }
}

