
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from "@angular/core";
import {RouterModule} from "@angular/router";
import {NutaskSharedModule} from "../shared";
import {coursesState} from "./courses.route";

import {CourseMgmtComponent} from "./courses/course-management.component";
import {CourseDialogComponent, CourseMgmtDialogComponent} from "./courses/course-management-dialog.component";
import {
    CourseDeleteDialogComponent,
    CourseMgmtDeleteDialogComponent
} from "./courses/course-management-delete-dialog.component";
import {CourseMgmtDetailComponent} from "./courses/course-management-detail.component";

import {CourseModalService} from "./courses/course-modal.service";
import {CourseResolve, CourseResolvePagingParams} from "./courses/course-management.route";

@NgModule({
    imports: [
        NutaskSharedModule,
        RouterModule.forChild(coursesState),
        /* jhipster-needle-add-admin-module - JHipster will add admin modules here */
    ],
    declarations: [
        CourseMgmtComponent,
        CourseDialogComponent,
        CourseDeleteDialogComponent,
        CourseMgmtDetailComponent,
        CourseMgmtDialogComponent,
        CourseMgmtDeleteDialogComponent,
    ],
    entryComponents: [
        CourseMgmtDialogComponent,
        CourseMgmtDeleteDialogComponent
    ],
    providers: [
        CourseResolvePagingParams,
        CourseResolve,
        CourseModalService
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class NutaskCoursesModule {}
