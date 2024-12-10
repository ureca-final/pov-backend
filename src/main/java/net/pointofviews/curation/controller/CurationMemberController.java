package net.pointofviews.curation.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.curation.controller.specification.CurationMemberSpecification;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/curations")
@RequiredArgsConstructor
public class CurationMemberController implements CurationMemberSpecification {

}
