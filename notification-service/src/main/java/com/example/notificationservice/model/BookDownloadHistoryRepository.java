package com.example.notificationservice.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookDownloadHistoryRepository extends CrudRepository<BookDownloadHistoryItem, Long> {
}
