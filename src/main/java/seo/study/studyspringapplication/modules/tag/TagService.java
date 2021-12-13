package seo.study.studyspringapplication.modules.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
