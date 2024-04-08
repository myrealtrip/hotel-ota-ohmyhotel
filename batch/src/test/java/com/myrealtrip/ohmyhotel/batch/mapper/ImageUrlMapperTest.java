package com.myrealtrip.ohmyhotel.batch.mapper;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Photo;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ImageUrlMapperTest {

    private ImageUrlMapper mapper = Mappers.getMapper(ImageUrlMapper.class);

    @Test
    void toImageUrlMap() {
        Photo photo = Photo.builder()
            .url("https://photos01.ohmyhotel.com/hotels/3000000/2840000/2835200/2835114/00a3dc31_z.jpg")
            .build();

        Map<Integer, String> imageUrlMap = mapper.toImageUrlMap(photo);

        assertThat(imageUrlMap.get(1000)).isEqualTo("https://photos01.ohmyhotel.com/hotels/3000000/2840000/2835200/2835114/00a3dc31_z.jpg");
        assertThat(imageUrlMap.get(350)).isEqualTo("https://photos01.ohmyhotel.com/hotels/3000000/2840000/2835200/2835114/00a3dc31_b.jpg");
        assertThat(imageUrlMap.get(200)).isEqualTo("https://photos01.ohmyhotel.com/hotels/3000000/2840000/2835200/2835114/00a3dc31_s.jpg");
        assertThat(imageUrlMap.get(70)).isEqualTo("https://photos01.ohmyhotel.com/hotels/3000000/2840000/2835200/2835114/00a3dc31_t.jpg");
    }
}