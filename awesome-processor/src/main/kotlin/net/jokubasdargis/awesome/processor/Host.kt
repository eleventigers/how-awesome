package net.jokubasdargis.awesome.processor

enum class Host(internal val value: String) {
    GITHUB("github.com");

    fun apply(host: String?) : Boolean {
        return value.equals(host);
    }

    companion object {
        fun from(value : String?) : Host? {
            values().forEach {
                if (it.value.equals(value)) {
                    return it
                }
            }
            return null;
        }
    }
}
