package com.sia.codingtest.controller

import com.sia.codingtest.domain.Aoi
import com.sia.codingtest.domain.Point
import com.sia.codingtest.dto.response.AoiResponse
import com.sia.codingtest.dto.response.AoisDto
import com.sia.codingtest.dto.request.CreateAoiDto
import com.sia.codingtest.dto.response.IdResponse

import com.sia.codingtest.service.AoiService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AoiController(private val aoiService: AoiService) {

    // 관심 지역 등록
    @PostMapping("/aois")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveAoi(@RequestBody @Validated createAoiDto: CreateAoiDto) : ResponseEntity<IdResponse> {
        val id = aoiService.saveAoi(createAoiDto)
        return ResponseEntity.ok().body(IdResponse(id))
    }

    // 행정 지역에 포함되는 관심 지역 조회
    @GetMapping("/regions/{regionId}/aois/intersects")
    @ResponseStatus(HttpStatus.OK)
    fun findAoisInRegion(@PathVariable regionId: Long) : AoiResponse<List<AoisDto>> {
        val aois : List<Aoi>? = aoiService.findAoiInRegion(regionId)
        val aoisDto : ArrayList<AoisDto> = ArrayList()

        return if (aois != null) {
            aois.forEach{ aoi ->
                aoisDto.add(AoisDto(aoi.id,aoi.name,Point.convertPolygonToList(aoi.area)))
            }
            AoiResponse(aoisDto)
        } else {
            AoiResponse(listOf())
        }
    }

    // 가장 가까운 관심 지역 조회
    @GetMapping("/aois")
    @ResponseStatus(HttpStatus.OK)
    fun findClosestAoi(@RequestParam(value = "lat") lat: Double, @RequestParam(value = "long") long : Double ): AoiResponse<List<AoisDto>> {
        val aoi = aoiService.findClosestAoi(lat, long)
        return if (aoi != null) {
            AoiResponse(listOf(AoisDto(aoi.id,aoi.name,Point.convertPolygonToList(aoi.area))))
        } else {
            AoiResponse(listOf())
        }
    }
}