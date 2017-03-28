package net.michalsitko

object CirceOptics {
  import cats.syntax.either._
  import io.circe._, io.circe.parser._

  val json: Json = parse("""
    {
      "order": {
        "customer": {
          "name": "Custy McCustomer",
          "contactDetails": [{
            "address": "1 Fake Street, London, England",
            "phone": "0123-456-789"
          },
          {
             "address": "1 Fake Street, London, England",
             "phone": "555-666-777"
           }]
        },
        "items": [{
          "id": 123,
          "description": "banana",
          "quantity": 1
        }, {
          "id": 456,
          "description": "apple",
          "quantity": 2
        }],
        "total": 123.45
      }
    }
    """).getOrElse(Json.Null)

  def main(args: Array[String]): Unit = {
    import io.circe.optics.JsonPath._
    // import io.circe.optics.JsonPath._

    val _phoneNum = root.order.customer.contactDetails.phone.string
    // _phoneNum: monocle.Optional[io.circe.Json,String] = monocle.POptional$$anon$1@4f5b626d

    val phoneNum: Option[String] = _phoneNum.getOption(json)

    println("here: " + phoneNum)
  }
}
