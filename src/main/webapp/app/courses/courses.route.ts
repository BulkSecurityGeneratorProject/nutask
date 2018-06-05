import { Routes } from '@angular/router';


import { UserRouteAccessService } from '../shared';
import {courseDialogRoute, courseMgmtRoute} from "./courses/course-management.route";

const COURSES_ROUTES = [
    ...courseMgmtRoute,

];

export const coursesState: Routes = [{
    path: '',
    data: {
        authorities: ['ROLE_ADMIN']
    },
    canActivate: [UserRouteAccessService],
    children: COURSES_ROUTES
},
    ...courseDialogRoute
];
