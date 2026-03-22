package com.typingstatsvit.api.controllers;

import com.typingstatsvit.api.entity.DailyQuote;
import com.typingstatsvit.api.service.SyncService;
import com.typingstatsvit.api.service.TypeggSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TypeggSyncService typeggSyncService;
    private final SyncService syncService;

    public AdminController(TypeggSyncService typeggSyncService, SyncService syncService) {
        this.typeggSyncService = typeggSyncService;
        this.syncService = syncService;
    }

    @PostMapping("/typegg/quote/sync")
    public ResponseEntity<Map<String, String>> manualQuoteSync() {

        // This will throw a 500 error if TypeGG is down, which Spring will send to the client
        DailyQuote quote = typeggSyncService.forceFetchAndSaveDailyQuote();

        return ResponseEntity.ok(Map.of(
                "message", "Successfully fetched and saved quote.",
                "quoteId", quote.getQuoteId()
        ));
    }
}
