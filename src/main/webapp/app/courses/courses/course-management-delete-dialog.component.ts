import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import {CourseService} from '../../shared';
import {CourseModalService} from "./course-modal.service";
import {Course} from "../../shared/course/course.model";

@Component({
    selector: 'jhi-course-mgmt-delete-dialog',
    templateUrl: './course-management-delete-dialog.component.html'
})
export class CourseMgmtDeleteDialogComponent {

    course: Course;

    constructor(
        private courseService: CourseService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id) {
        this.courseService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({ name: 'courseListModification',
                content: 'Deleted a course'});
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-course-delete-dialog',
    template: ''
})
export class CourseDeleteDialogComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private courseModalService: CourseModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.courseModalService.open(CourseMgmtDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
