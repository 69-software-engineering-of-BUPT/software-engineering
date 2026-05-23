package com.bupt.tarecruit.service;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.bupt.tarecruit.model.Application;
import com.bupt.tarecruit.model.ApplicationView;

public class ApplicationServiceTest {
    private final ApplicationService applicationService = new ApplicationService();

    // ------------------------------------------------------------------ //
    // submitApplication — pure validation (no file I/O needed)           //
    // ------------------------------------------------------------------ //

    @Test(expected = RuntimeException.class)
    public void submitApplicationThrowsWhenStatementExceedsCharacterLimit() throws Exception {
        Application app = new Application();
        app.setStudentId("TA999");
        app.setJobId("JOB999");
        app.setStatement("x".repeat(501)); // exceeds 500-char limit

        // Validation fires before any repository call
        applicationService.submitApplication(app);
    }

    // ------------------------------------------------------------------ //
    // submitApplication — duplicate check (reads seed data)             //
    // ------------------------------------------------------------------ //

    @Test(expected = RuntimeException.class)
    public void submitApplicationThrowsWhenDuplicateApplicationExists() throws Exception {
        // Seed data: TA001 already has APP001 targeting JOB001
        Application app = new Application();
        app.setStudentId("TA001");
        app.setJobId("JOB001");
        app.setStatement("Valid statement for duplicate test.");

        applicationService.submitApplication(app);
    }

    // ------------------------------------------------------------------ //
    // getTAApplicationList — reads seed data for TA001                  //
    // ------------------------------------------------------------------ //

    @Test
    public void getTaApplicationListReturnsApplicationsForTA001() throws Exception {
        List<ApplicationView> views = applicationService.getTAApplicationList("TA001");

        assertFalse(views.isEmpty());
        assertNotNull(views.get(0).getApplicationId());
        assertNotNull(views.get(0).getJobId());
    }

    @Test
    public void getTaApplicationListApplicationsAreOwnedByTA001() throws Exception {
        List<ApplicationView> views = applicationService.getTAApplicationList("TA001");

        for (ApplicationView view : views) {
            assertNotNull(view.getStatus());
            assertNotNull(view.getApplyTime());
        }
    }

    // ------------------------------------------------------------------ //
    // TA005: role selection — applicationType stored in application record //
    // ------------------------------------------------------------------ //

    @Test
    public void getTaApplicationListContainsApplicationTypeForRoleSelection() throws Exception {
        // Seed data: TA001 has UUID applications with applicationType = "L" (leader)
        List<ApplicationView> views = applicationService.getTAApplicationList("TA001");

        boolean hasApplicationType = false;
        for (ApplicationView view : views) {
            if (view.getApplicationType() != null) {
                hasApplicationType = true;
                break;
            }
        }
        assertTrue("Expected at least one application with a non-null applicationType (leader/non-leader)",
                hasApplicationType);
    }

    // ------------------------------------------------------------------ //
    // updateStatementFromChat — message length validation (reads APP001) //
    // ------------------------------------------------------------------ //

    @Test(expected = RuntimeException.class)
    public void updateStatementFromChatThrowsWhenMessageExceedsCharacterLimit() throws Exception {
        // APP001 exists in seed data; length check fires before save
        applicationService.updateStatementFromChat("APP001", "y".repeat(501));
    }

    // ------------------------------------------------------------------ //
    // TA002 AC2: application list view includes moduleName & feedback     //
    // getTAApplicationList() joins Job data to populate view fields       //
    // ------------------------------------------------------------------ //

    @Test
    public void getTaApplicationListViewIncludesModuleNameAndFeedback() throws Exception {
        List<ApplicationView> views = applicationService.getTAApplicationList("TA001");

        assertFalse("TA001 should have at least one application", views.isEmpty());

        // At least one view must carry a populated moduleName from the job lookup
        boolean hasModuleName = false;
        for (ApplicationView view : views) {
            if (view.getModuleName() != null && !view.getModuleName().isEmpty()) {
                hasModuleName = true;
                break;
            }
        }
        assertTrue("Expected at least one ApplicationView with populated moduleName from associated Job",
                hasModuleName);

        // feedback field must be present (may be empty string but not missing)
        boolean hasFeedback = false;
        for (ApplicationView view : views) {
            if (view.getFeedback() != null) {
                hasFeedback = true;
                break;
            }
        }
        assertTrue("Expected at least one ApplicationView with a non-null feedback field", hasFeedback);
    }

    // ------------------------------------------------------------------ //
    // TA005 AC1: submitting an application with applicationType="leader"  //
    // must persist the normalized role code so it can be read back        //
    // ------------------------------------------------------------------ //

    @Test
    public void submitApplicationWithLeaderTypeStoresApplicationType() throws Exception {
        Application app = new Application();
        app.setStudentId("TA_TEST_ROLE");
        app.setJobId("JOB001");
        app.setStatement("Applying as leader TA for unit test role check.");
        app.setApplicationType("leader");

        try {
            applicationService.submitApplication(app);

            // applicationId is assigned by submitApplication(); retrieve using it
            String appId = app.getApplicationId();
            assertNotNull("submitApplication must assign an applicationId", appId);

            List<ApplicationView> views = applicationService.getTAApplicationList("TA_TEST_ROLE");
            assertEquals("Should have exactly one application after submit", 1, views.size());
            assertEquals("L", views.get(0).getApplicationType());
            assertEquals("PENDING", views.get(0).getStatus());
        } finally {
            if (app.getApplicationId() != null) {
                new java.io.File("data/applications/" + app.getApplicationId() + "_application.json").delete();
            }
        }
    }
}
