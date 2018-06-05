import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { JhiPaginationUtil } from 'ng-jhipster';

import { Principal } from '../../shared';
import {CourseMgmtComponent} from "./course-management.component";
import {CourseMgmtDetailComponent} from "./course-management-detail.component";
import {CourseDialogComponent} from "./course-management-dialog.component";
import {CourseDeleteDialogComponent} from "./course-management-delete-dialog.component";

@Injectable()
export class CourseResolve implements CanActivate {

    constructor(private principal: Principal) { }

    canActivate() {
        return this.principal.identity().then((account) => this.principal.hasAnyAuthority(['ROLE_ADMIN', 'ROLE_USER']));
    }
}

@Injectable()
export class CourseResolvePagingParams implements Resolve<any> {

    constructor(private paginationUtil: JhiPaginationUtil) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
        const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
        return {
            page: this.paginationUtil.parsePage(page),
            predicate: this.paginationUtil.parsePredicate(sort),
            ascending: this.paginationUtil.parseAscending(sort)
        };
    }
}

export const courseMgmtRoute: Routes = [
    {
        path: 'course-management',
        component: CourseMgmtComponent,
        resolve: {
            'pagingParams': CourseResolvePagingParams
        },
        data: {
            pageTitle: "Course Management"
        }
    },
    {
        path: 'course-management/:login',
        component: CourseMgmtDetailComponent,
        data: {
            pageTitle: 'userManagement.home.title'
        }
    }
];

export const courseDialogRoute: Routes = [
    {
        path: 'course-management-new',
        component: CourseDialogComponent,
        outlet: 'popup'
    },
    {
        path: 'course-management/:id/edit',
        component: CourseDialogComponent,
        outlet: 'popup'
    },
    {
        path: 'course-management/:id/delete',
        component: CourseDeleteDialogComponent,
        outlet: 'popup'
    }
];
