package upf.pjt.cahier_de_textes.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import upf.pjt.cahier_de_textes.dao.entities.Entree;

import java.util.List;

@Service
public class EntreeService {
    public static Page<Entree> getEntries(int page, int size, List<Entree> entries) {
        Pageable pageRequest = PageRequest.of(page, size);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), entries.size());

        List<Entree> pageContent = entries.subList(start, end);
        return new PageImpl<>(pageContent, pageRequest, entries.size());
    }
}
