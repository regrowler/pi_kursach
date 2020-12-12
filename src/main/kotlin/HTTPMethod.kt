sealed class HTTPMethod(val name: String) {
    object POST : HTTPMethod("POST")
    object PUT : HTTPMethod("PUT")
    object GET : HTTPMethod("GET")
    object DELETE : HTTPMethod("DELETE")
    companion object {
        operator fun invoke(method: String): HTTPMethod? {
            return when (method) {
                POST.name -> POST
                PUT.name -> PUT
                GET.name -> GET
                DELETE.name -> DELETE
                else -> null
            }
        }
    }

}