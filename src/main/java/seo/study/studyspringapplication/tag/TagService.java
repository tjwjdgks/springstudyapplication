package seo.study.studyspringapplication.tag;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seo.study.studyspringapplication.domain.Tag;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public Tag findOrCreateNew(String tagtitle){
        Tag tag = tagRepository.findByTitle(tagtitle);
        if(tag == null){
            tag = tagRepository.save(Tag.builder().title(tagtitle).build());
        }
        return tag;
    }
}
