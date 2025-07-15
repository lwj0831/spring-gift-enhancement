package gift.wishlist.controller;

import gift.auth.annotation.LoginUser;
import gift.wishlist.dto.GetWishItemResponseDto;
import gift.wishlist.dto.RegisterWishItemRequestDto;
import gift.wishlist.service.WishItemService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wish-items")
public class WishItemController {

  private final WishItemService wishItemService;

  public WishItemController(WishItemService wishItemService) {
    this.wishItemService = wishItemService;
  }

  @GetMapping
  public ResponseEntity<List<GetWishItemResponseDto>> getWishItems(@LoginUser Long memberId) {
    List<GetWishItemResponseDto> wishItems = wishItemService.findWishItems(memberId);
    return ResponseEntity.ok(wishItems);
  }

  @PostMapping
  public ResponseEntity<Void> addWishItem(@LoginUser Long memberId,
      @Valid @RequestBody RegisterWishItemRequestDto dto) {
    Long id = wishItemService.registerWishItem(memberId, dto);
    URI uri = URI.create("/api/wish-items" + id);
    return ResponseEntity.created(uri).build();
  }

  @DeleteMapping("/{wishItemId}")
  public ResponseEntity<Void> deleteWishItem(@PathVariable(name = "wishItemId") Long id) {
    wishItemService.deleteWishItem(id);
    return ResponseEntity.noContent().build();
  }

}
