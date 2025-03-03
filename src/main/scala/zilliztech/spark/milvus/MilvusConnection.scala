package zilliztech.spark.milvus

import io.milvus.client.MilvusServiceClient
import io.milvus.param.ConnectParam

import scala.collection.mutable

case class MilvusConnection (
                            client: MilvusServiceClient) {
}

object MilvusConnection {
  private val cache = new mutable.HashMap[String, MilvusServiceClient]

  def acquire(milvusOptions: MilvusOptions): MilvusServiceClient = {
    lazy val connectParam = if (milvusOptions.uri.isEmpty) {
      val builder = ConnectParam.newBuilder
        .withHost(milvusOptions.host)
        .withPort(milvusOptions.port)
        .withAuthorization(milvusOptions.userName, milvusOptions.password)
        .withDatabaseName(milvusOptions.databaseName)
      
      if (milvusOptions.secure) {
        if (!milvusOptions.clientCert.isEmpty) {
          builder.withServerPemPath(milvusOptions.clientCert)
          builder.withServerName(milvusOptions.host)
        }
      }
      
      builder.build
    } else {
      // zilliz cloud
      val builder = ConnectParam.newBuilder
        .withUri(milvusOptions.uri)
        .withToken(milvusOptions.token)
      
      if (milvusOptions.secure) {
        if (!milvusOptions.clientCert.isEmpty) {
          builder.withServerPemPath(milvusOptions.clientCert)
          builder.withServerName(milvusOptions.host)
        }
      }
      
      builder.build
    }

    new MilvusServiceClient(connectParam)
//    cache.getOrElseUpdate(milvusOptions.toString, new MilvusServiceClient(connectParam))
  }
}