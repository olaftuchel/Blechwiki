package org.redderei.Blechwiki.gettersetter

data class AutoNrClass(
    val AutoNr: List<TblNr>
) {
    data class TblNr(
        val lastNr: Int,
        val tbl: String
    )
}