package com.rydvi.clean_vrn.ui.map

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions

class PolygonControl(map: GoogleMap, context: Context) {

    private val mMap = map
    private val context = context
    private var currentPolygon: Polygon? = null

    fun addPoint(location: LatLng): PolygonControl {
        if (currentPolygon === null) {
            currentPolygon = mMap.addPolygon(
                PolygonOptions().clickable(false).fillColor(Color.GRAY).add(location)
            )
        } else {
            val tempPoints = currentPolygon!!.points
            //Из полученного списка удаляем последний полигон, поскольку он генерируется
            //для связи с первой точки (образует замкнутость) и равен координатам первой точки.
            if (tempPoints.size > 2) {
                tempPoints.removeAt(tempPoints.size - 1)
            }
            tempPoints.add(location)
            currentPolygon!!.points = tempPoints
        }
        return this
    }

    fun removeLastPoint(): PolygonControl {
        currentPolygon?.let { curPolygon ->
            val tempPoints = curPolygon.points
            if (tempPoints.size > 2) {
                //Удаляем последний элемент, который генерируется для замыкания
                tempPoints.removeAt(tempPoints.size - 1)
                //Удаляем последний элемент
                tempPoints.removeAt(tempPoints.size - 1)
                curPolygon.points = tempPoints
            } else {
                //Если элементов меньше 2, то там только точка и это значит, что
                // необходимо удалить полигон полностью
                currentPolygon?.remove()
                //С первого раза не удаляет
                currentPolygon?.remove()
                currentPolygon = null
            }
        }
        return this
    }

    fun removePolygon(): PolygonControl {
        currentPolygon?.remove()
        //С первого раза не удаляет
        currentPolygon?.remove()
        currentPolygon = null
        return this
    }

    fun removePolygon(polygon: Polygon) {
        polygon.remove()
        //С первого раза не удаляет
        polygon.remove()
    }

    fun endBuild(): Polygon? {
        val buildedPolygon = currentPolygon
        currentPolygon = null
        return buildedPolygon
    }

    fun getCurrentPolygon() = currentPolygon

    fun createFromPoints(googlePoints: Array<LatLng>): Polygon? {
        var polygonOptions = PolygonOptions().clickable(false).fillColor(Color.GRAY)
        for (point in googlePoints) {
            polygonOptions.add(point)
        }
        return mMap.addPolygon(polygonOptions)
    }
}