package com.it52.eventservice.service.impl;

import com.it52.eventservice.model.Author;
import com.it52.eventservice.model.Event;
import com.it52.eventservice.repository.AuthorRepository;
import com.it52.eventservice.service.api.AuthorService;
import com.it52.eventservice.service.api.EventRegistrationServiceClient;
import com.it52.eventservice.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final EventRegistrationServiceClient eventRegistrationServiceClient;

    @Override
    public Author getOrCreateAuthor() {
        String sub = SecurityUtils.getCurrentUserId();
        return authorRepository.findBySub(sub)
                .orElseGet(() -> authorRepository.save(
                        Author.builder()
                                .sub(sub)
                                .authorName(SecurityUtils.getCurrentUsername())
                                .email(SecurityUtils.getCurrentUserEmail())
                                .avatar("getAvatar()")
                                .build()
                ));
    }

/*    public String getAvatar() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken) {
            String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
            String sub = ((JwtAuthenticationToken) authentication).getToken().getSubject();

            var user = eventRegistrationServiceClient.getBySub(token, sub);
            if (user == null) {
                throw new IllegalArgumentException("User does not exist");
            }
            return user.getAvatarImage();
        } else {
            throw new IllegalStateException("No JWT authentication found in context");
        }
    }*/

    @Override
    public String getAuthorName(Event event) {
        if (event.getAuthor() == null) {
            return "Автор остался в другой таблице";
        }

        String sub = event.getAuthor().getSub();
        Author author = authorRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("Автор с sub=" + sub + " не найден"));

        return author.getAuthorName();
    }
}
