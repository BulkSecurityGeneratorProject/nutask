import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Subscription';

import { User, UserService } from '../../shared';

@Component({
    selector: 'jhi-course-mgmt-detail',
    templateUrl: './course-management-detail.component.html'
})
export class CourseMgmtDetailComponent implements OnInit, OnDestroy {

    user: User;
    private subscription: Subscription;

    constructor(
        private userService: UserService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['login']);
        });
    }

    load(login) {
        this.userService.find(login).subscribe((response) => {
            this.user = response.body;
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

}
