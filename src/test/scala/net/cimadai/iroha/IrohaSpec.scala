package net.cimadai.iroha

import java.net.URI
import java.util.Base64

import com.wordnik.swagger.client.SwaggerConfig
import io.swagger.client.IrohaRestApiClient
import io.swagger.client.model._
import org.scalatest.FunSpec

import com.wordnik.swagger.client.ClientResponseReaders.Json4sFormatsReader._
import com.wordnik.swagger.client.RequestWriters.Json4sFormatsWriter._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.language.postfixOps

class IrohaSpec extends FunSpec {
  implicit val formats = org.json4s.DefaultFormats
  implicit val reader = JsonFormatsReader
  implicit val writer = JsonFormatsWriter

  private val base64Encoder = Base64.getEncoder
  private val client = new IrohaRestApiClient(SwaggerConfig.forUrl(URI.create("http://localhost:8080")))

  describe("IrohaSpec") {
    it("account api run right") {
      val keyPair = Iroha.createKeyPair()
      val pubKeyBase64 = base64Encoder.encodeToString(keyPair.publicKey.getAbyte)

      {
        val ret = Await.result(client.account.accountGetAll(), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = DeleteAccountRequest(TransactionRequest(pubKeyBase64, "test-signature", System.currentTimeMillis().toString))
        val ret = Await.result(client.account.accountDeleteByUsername("domain", "username", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = DeleteAccountRequest(TransactionRequest(pubKeyBase64, "test-signature", System.currentTimeMillis().toString))
        val ret = Await.result(client.account.accountDeleteByUsernameFromDefaultDomain("username", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = DeleteAccountRequest(TransactionRequest(pubKeyBase64, "test-signature", System.currentTimeMillis().toString))
        val ret = Await.result(client.account.accountDeleteByUUID("abcd0123456789012345678901234567", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val ret = Await.result(client.account.accountGetByUsername("domain", "username"), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val ret = Await.result(client.account.accountGetByUsernameFromDefaultDomain("username"), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val ret = Await.result(client.account.accountGetByUUID("abcd0123456789012345678901234567"), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = UpdateAccountRequest(TransactionRequest(pubKeyBase64, "test-signature", System.currentTimeMillis().toString), "username")
        val ret = Await.result(client.account.accountUpdateByUsernameFromDefaultDomain("username", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = UpdateAccountRequest(TransactionRequest(pubKeyBase64, "test-signature", System.currentTimeMillis().toString), "username")
        val ret = Await.result(client.account.accountUpdateByUsername("domain", "username", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = UpdateAccountRequest(TransactionRequest(pubKeyBase64, "test-signature", System.currentTimeMillis().toString), "username")
        val ret = Await.result(client.account.accountUpdateByUUID("abcd0123456789012345678901234567", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val account = AccountPayload(1, List(pubKeyBase64), Some("user1"), "abcd0123456789012345678901234567")
        val request = AddAccountRequest(account, TransactionRequest(pubKeyBase64, "test-signature", System.currentTimeMillis().toString))
        val ret = Await.result(client.account.accountAdd(request), Duration.Inf)
        assert(ret.code == 0, "must be right")
      }
    }

    it("currency api run right") {
      val keyPair = Iroha.createKeyPair()
      val pubKeyBase64 = base64Encoder.encodeToString(keyPair.publicKey.getAbyte)

      {
        val currency = CurrencyPayload(description = Some("This is test"), domain_name = "com.example", ledger_name = "testledger", name = "TEST", value = 100.0)
        val request = CreateCurrencyRequest(pubKeyBase64, currency, "signature", "target", System.currentTimeMillis().toString)
        val ret = Await.result(client.currency.currencyAdd(request), Duration.Inf)
        assert(ret.code == 0, "must be right")
      }

      {
        val request = CurrencyValueRequest(pubKeyBase64, "signature", "target", System.currentTimeMillis().toString, 200.0)
        val ret = Await.result(client.currency.currencyAddValue("currency_uri", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val ret = Await.result(client.currency.currencyGetAll(pubKeyBase64, "currency_uri", "target"), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = CurrencyValueRequest(pubKeyBase64, "signature", "target", System.currentTimeMillis().toString, 200.0)
        val ret = Await.result(client.currency.currencySubtractValue("currency_uri", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = CurrencyTransferRequest(pubKeyBase64, "receiver", "sender", "signature", "target", System.currentTimeMillis().toString, 200.0)
        val ret = Await.result(client.currency.currencyTransfer("currency_uri", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = UpdateCurrencyRequest(pubKeyBase64, "description", "signature", System.currentTimeMillis().toString)
        val ret = Await.result(client.currency.currencyUpdate("currency_uri", request), Duration.Inf)
        assert(ret != null, "must be right")
      }
    }

    it("quorum api run right") {
      val keyPair = Iroha.createKeyPair()
      val pubKeyBase64 = base64Encoder.encodeToString(keyPair.publicKey.getAbyte)

      {
        // TODO: Why quorum is Long? Max quorum is 64...
        // TODO: Why timestamp is String? This should be Long.
        val request = UpdateQuorumRequest(pubKeyBase64, 2, "signature", System.currentTimeMillis().toString)
        val ret = Await.result(client.quorum.quorumUpdate("abcd0123456789012345678901234567", request), Duration.Inf)
        assert(ret != null, "must be right")
      }
    }

    it("signatories api run right") {
      val keyPair = Iroha.createKeyPair()
      val pubKeyBase64 = base64Encoder.encodeToString(keyPair.publicKey.getAbyte)

      {
        val request = SignatoryRequest(pubKeyBase64, List("sig1", "sig2"), "signature", System.currentTimeMillis().toString)
        val ret = Await.result(client.signatories.signatoriesAdd("abcd0123456789012345678901234567", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val request = DeleteSignatoryRequest(pubKeyBase64, "signature", System.currentTimeMillis().toString)
        val ret = Await.result(client.signatories.signatoriesDelete("sig", "abcd0123456789012345678901234567", request), Duration.Inf)
        assert(ret != null, "must be right")
      }

      {
        val ret = Await.result(client.signatories.signatoriesGetAll("abcd0123456789012345678901234567"), Duration.Inf)
        assert(ret != null, "must be right")
      }
    }

    it("transactions api run right") {
      val keyPair = Iroha.createKeyPair()
      val pubKeyBase64 = base64Encoder.encodeToString(keyPair.publicKey.getAbyte)

      {
        val ret = Await.result(client.transactions.transactionsGetAll(pubKeyBase64, "currency_uri", "target"), Duration.Inf)
        assert(ret != null, "must be right")
      }

    }

//    it("verify run right") {
//      val keyPair = Iroha.createKeyPair()
//      val message = "This is test string".getBytes()
//      assert(Iroha.verify(keyPair, Iroha.sign(keyPair, message), message), true)
//    }
//
//    it("account creation run right") {
//      val keyPair = Iroha.createKeyPair()
//      val pubKeyBase64 = base64Encoder.encodeToString(keyPair.publicKey.getAbyte)
//      val assets = Seq("my_asset")
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
//    }
//
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
