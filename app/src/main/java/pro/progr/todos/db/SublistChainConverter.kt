package pro.progr.doflow.db

import androidx.room.TypeConverter

class SublistChainConverter {

    @TypeConverter
    fun toSublistChain(sublistString : String) : SublistChain {
        return SublistChain(sublistsString = sublistString)
    }

    @TypeConverter
    fun fromSublistChain(sublistChain: SublistChain) : String {
        return sublistChain.sublistsString
    }

}