import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import {CourseService, JhiLanguageHelper, User, UserService} from '../../shared';
import {Course} from "../../shared/course/course.model";
import {CourseModalService} from "./course-modal.service";

@Component({
    selector: 'jhi-course-mgmt-dialog',
    templateUrl: './course-management-dialog.component.html'
})
export class CourseMgmtDialogComponent implements OnInit {

    user: User;
    languages: any[];
    authorities: any[];
    students: User[];
    allStudents: any[];
    course: Course;
    isSaving: Boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private languageHelper: JhiLanguageHelper,
        private userService: UserService,
        private courseService: CourseService,
        private eventManager: JhiEventManager
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.authorities = [];
        this.students = this.course.students;
        this.userService.authorities().subscribe((authorities) => {
            this.authorities = authorities;
        });
        this.userService.query().subscribe((students) => {
            this.students = students.body;
        });
        if(this.course.id){
            this.userService.queryByCourse(this.course.id).subscribe((students) => {
                this.students = students.body;

                this.userService.query().subscribe((students) => {
                    this.allStudents = students.body;
                    this.course.students.forEach(student => {
                        this.allStudents = this.allStudents.filter(user => {
                            return user.id !== student.id;
                        });
                    });
                });
            });
        } else{
            this.userService.query().subscribe((students) => {
                this.allStudents = students.body;
            });
        }
        this.languageHelper.getAll().then((languages) => {
            this.languages = languages;
        });


    }

    addStudent(student: User){
        console.log("include student:", student);
        for(let i=0; i<this.course.students.length; i++){
            if(this.course.students[i].id === student.id){
                return;
            }
        }
        this.course.students.push(student);
        this.allStudents = this.allStudents.filter(user => {
            if(user.id === student.id){
                return false;
            }
            return true;
        })
    }

    removeStudent(student: User){
        console.log("remove student:", student);
        for(let i=0; i<this.allStudents.length; i++){
            if(this.allStudents[i].id === student.id){
                return;
            }
        }
        this.allStudents.push(student);
        this.course.students = this.course.students.filter(user => {
            if(user.id === student.id){
                return false;
            }
            return true;
        })
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.course.id !== null) {
            this.courseService.update(this.course).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        } else {
            this.courseService.create(this.course).subscribe((response) => this.onSaveSuccess(response), () => this.onSaveError());
        }
    }

    private onSaveSuccess(result) {
        this.eventManager.broadcast({ name: 'courseListModification', content: 'OK' });
        this.isSaving = false;
        this.activeModal.dismiss(result.body);
    }

    private onSaveError() {
        this.isSaving = false;
    }
}

@Component({
    selector: 'jhi-course-dialog',
    template: ''
})
export class CourseDialogComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private courseModalService: CourseModalService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.courseModalService.open(CourseMgmtDialogComponent as Component, params['id']);
            } else {
                this.courseModalService.open(CourseMgmtDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
