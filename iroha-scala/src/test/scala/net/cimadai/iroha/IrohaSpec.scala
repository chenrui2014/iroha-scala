package net.cimadai.iroha

import java.util.Base64

import com.google.flatbuffers.FlatBufferBuilder
import io.grpc.ManagedChannelBuilder
import iroha.SmeragiGrpc.MyBlockingStub
import iroha._
import org.scalatest.FunSpec

class IrohaSpec extends FunSpec {
  private val base64Encoder = Base64.getEncoder
  private lazy val channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext(true).build()
  private lazy val sumeragiGrpc = new MyBlockingStub(channel)
//  private lazy val sumeragiGrpc = SumeragiGrpc.blockingStub(channel)
//  private lazy val assetRepoGrpc = AssetRepositoryGrpc.blockingStub(channel)
//  private lazy val transactionRepoGrpc = TransactionRepositoryGrpc.blockingStub(channel)

  describe("IrohaSpec") {
    it("verify run right") {
      val keyPair = Iroha.createKeyPair()
      val message = "This is test string".getBytes()
      assert(Iroha.verify(keyPair, Iroha.sign(keyPair, message), message), true)
    }

    def createAccountAddTx(alias: String, attachmentMime: String, attachmentData: Array[Byte]): Array[Byte] = {
      val keyPair = Iroha.createKeyPair()
      val pubKeyBase64 = base64Encoder.encodeToString(keyPair.publicKey.getAbyte)

      val fbb = new FlatBufferBuilder(0)
      val pubKeyOffset = fbb.createString(pubKeyBase64)
      val aliasOffset = fbb.createString(alias)
      val pubkeyListOffset = fbb.createVectorOfTables(Array(pubKeyOffset))
      val accountOffset = Account.createAccount(fbb, pubKeyOffset, aliasOffset, pubkeyListOffset, 0)
      val accountAddOffset = AccountAdd.createAccountAdd(fbb, accountOffset)

      val attachMimeOffset = fbb.createString(attachmentMime)
      val attachDataOffset = fbb.createByteVector(attachmentData)
      val attachmentOffset = Attachment.createAttachment(fbb, attachMimeOffset, attachDataOffset)

      val signature = Iroha.sign(keyPair, alias.getBytes())
      val sigSignatureOffset = fbb.createByteVector(signature)
      val sigOffset = Signature.createSignature(fbb, pubKeyOffset, sigSignatureOffset, System.currentTimeMillis())

      val signatureListOffset = fbb.createVectorOfTables(Array(sigOffset))
      val hashOffset = fbb.createByteVector(base64Encoder.encode(alias.getBytes))

      fbb.finish(Transaction.createTransaction(
        fbb, pubKeyOffset, Command.AccountAdd, accountAddOffset, signatureListOffset, hashOffset, attachmentOffset))

      fbb.sizedByteArray()
    }

    it("account creation run right") {
      val txBytes = createAccountAddTx("User1", "", Array(0x0.toByte))
      println("<send>: " + txBytes.map("%02X" format _).mkString)
      val response = sumeragiGrpc.torii(txBytes)
      println("<recv>: " + response.getByteBuffer.array().map("%02X" format _).mkString)
      println(s"<resp>: code = ${response.code()}, message = ${response.message()}")

//      println(reTx.creatorPubKey())
//      val tblObj = reTx.command(new Table)
//      val re_re_accAdd = AccountAdd.getRootAsAccountAdd(tblObj.getByteBuffer)
//      val re_re_re_acc = re_re_accAdd.AccountAsIroha()
//      println(re_re_re_acc.pubKey())
//      println(re_re_re_acc.signatoriesLength())
//      println(re_re_re_acc.signatories(0))
//      println(re_re_re_acc.alias())
//      println(re_re_re_acc.useKeys())
//      println("OK3")

//      val account = Account(publicKey = pubKeyBase64, name = "user1", assets = assets)
//      val asset = Asset(name = "my_asset", value = Map("value" -> BaseObject(ValueInt(100))))
//
//      // create account
//      val ret1 = sumeragiGrpc.torii(Iroha.createTransaction(MethodType.ADD, pubKeyBase64).withAccount(account))
//      // create asset
//      val ret2 = sumeragiGrpc.torii(Iroha.createTransaction(MethodType.ADD, pubKeyBase64).withAsset(asset))
//
//      // text
//      val textAsset = Iroha.createAsset("text_asset", 10, Map("message" -> BaseObject(ValueString("some message."))))
//      val ret3 = sumeragiGrpc.torii(Iroha.createTransaction(MethodType.ADD, pubKeyBase64).withAsset(textAsset))
//
//      assert(ret1.value == "OK", "must be OK.")
//      assert(ret2.value == "OK", "must be OK.")
//      assert(ret3.value == "OK", "must be OK.")
//
//      val ret10 = assetRepoGrpc.find(Iroha.createAssetQuery(pubKeyBase64, "text_asset"))
//      println(ret10)
    }

//    it("transfer run right") {
//      val keyPair1 = Iroha.createKeyPair()
//      val keyPair2 = Iroha.createKeyPair()
//
//      val pubKeyBase64_1 = base64Encoder.encodeToString(keyPair1.publicKey.getAbyte)
//      val pubKeyBase64_2 = base64Encoder.encodeToString(keyPair2.publicKey.getAbyte)
//
//      val asset = Iroha.createAsset("my_asset", 100)
//      val assets = Seq("my_asset")
//      val account1 = Iroha.createAccount(pubKeyBase64_1, "user1", assets)
//      val account2 = Iroha.createAccount(pubKeyBase64_2, "user2", assets)
//
//      // prepare two accounts with assets
//      val ret01 = sumeragiGrpc.torii(Iroha.createTransaction(MethodType.ADD, pubKeyBase64_1).withAccount(account1))
//      val ret02 = sumeragiGrpc.torii(Iroha.createTransaction(MethodType.ADD, pubKeyBase64_1).withAsset(asset))
//      val ret03 = sumeragiGrpc.torii(Iroha.createTransaction(MethodType.ADD, pubKeyBase64_2).withAccount(account2))
//      val ret04 = sumeragiGrpc.torii(Iroha.createTransaction(MethodType.ADD, pubKeyBase64_2).withAsset(asset))
//      assert(ret01.value == "OK", "account creation must be success.")
//      assert(ret02.value == "OK", "asset creation must be success.")
//      assert(ret03.value == "OK", "account creation must be success.")
//      assert(ret04.value == "OK", "asset creation must be success.")
//
//      // wait for consensus completion
//      Thread.sleep(20000) // TODO: FIXME. Please fix more smart way to wait for consensus completion.
//
//      // check account existence
//      val ret05 = assetRepoGrpc.find(Iroha.createAccountQuery(pubKeyBase64_1))
//      val ret06 = assetRepoGrpc.find(Iroha.createAccountQuery(pubKeyBase64_2))
//      assert(ret05.message == "OK", "account finding must be success.")
//      assert(ret06.message == "OK", "account finding must be success.")
//
//      // check account existence and amount
//      val ret07 = assetRepoGrpc.find(Iroha.createAssetQuery(pubKeyBase64_1, "my_asset"))
//      val ret08 = assetRepoGrpc.find(Iroha.createAssetQuery(pubKeyBase64_2, "my_asset"))
//      assert(ret07.asset.isDefined, "asset must be defined.")
//      assert(ret07.asset.get.value("value").getValueInt == 100, "asset's amount must be 100.")
//      assert(ret08.asset.isDefined, "asset must be defined.")
//      assert(ret08.asset.get.value("value").getValueInt == 100, "asset's amount must be 100.")
//
//      val transferAsset = Iroha.createAsset("my_asset", 20, Map("message" -> BaseObject(ValueString("This is send for something."))))
//      val transferTransaction = Iroha.createTransaction(MethodType.TRANSFER, pubKeyBase64_1)
//        .withReceivePubkey(pubKeyBase64_2)
//        .withAsset(transferAsset)
//
//      // transfer asset from account1 to account2
//      val ret09 = sumeragiGrpc.torii(transferTransaction)
//      assert(ret09.value == "OK", "transfer must be success.")
//
//      // wait for consensus completion
//      Thread.sleep(20000) // TODO: FIXME. Please fix more smart way to wait for consensus completion.
//
//      // check account existence and amount
//      val ret10 = assetRepoGrpc.find(Iroha.createAssetQuery(pubKeyBase64_1, "my_asset"))
//      val ret11 = assetRepoGrpc.find(Iroha.createAssetQuery(pubKeyBase64_2, "my_asset"))
//      assert(ret10.asset.isDefined, "asset must be defined.")
//      assert(ret10.asset.get.value("value").getValueInt == 80, "asset's amount must be 80.")
//      assert(ret11.asset.isDefined, "asset must be defined.")
//      assert(ret11.asset.get.value("value").getValueInt == 120, "asset's amount must be 120.")
//
//      val ret12 = transactionRepoGrpc.find(Iroha.createAccountQuery(pubKeyBase64_1))
//
//      assert(ret12.transaction.nonEmpty, "history is not empty")
//    }
  }
}
