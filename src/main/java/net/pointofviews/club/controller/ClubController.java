package net.pointofviews.club.controller;

import net.pointofviews.club.dto.response.ReadAllClubsListResponse;
import net.pointofviews.club.dto.response.ReadClubDetailsResponse;
import net.pointofviews.club.dto.response.ReadMyClubsListResponse;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clubs")
public class ClubController implements ClubSpecification{

    @GetMapping
    @Override
    public ResponseEntity<BaseResponse<ReadAllClubsListResponse>> readAllClubs() {
        return null;
    }

    @GetMapping("/{clubId}")
    @Override
    public ResponseEntity<BaseResponse<ReadClubDetailsResponse>> readClubDetails(String clubId) {
        return null;
    }

    @GetMapping("/myclub")
    @Override
    public ResponseEntity<BaseResponse<ReadMyClubsListResponse>> readMyClubs() {
        return null;
    }
}
