package com.diplom.map.esri.model

class ESRIFeature {
    val features = ArrayList<ESRISubFeature>()
    var classcode: String = ""
    val featuresData = ArrayList<ESRIFeatureData>()

    fun addFeature(subFeature: ESRISubFeature) {
        features.add(subFeature)
    }

    fun addFeatureData(featureData: ESRIFeatureData) {
        featuresData.add(featureData)
        if (featureData.columnName.toLowerCase().contains("classcode"))
            classcode = featureData.value
    }
}