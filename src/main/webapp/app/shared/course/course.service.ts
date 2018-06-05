import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { SERVER_API_URL } from '../../app.constants';
import { createRequestOption } from '../model/request-util';
import {Course} from "./course.model";
import {User} from "../user/user.model";

@Injectable()
export class CourseService {
    private resourceUrl = SERVER_API_URL + 'api/courses';

    constructor(private http: HttpClient) { }

    create(course: Course): Observable<HttpResponse<Course>> {
        return this.http.post<Course>(this.resourceUrl, course, { observe: 'response' });
    }

    update(course: Course): Observable<HttpResponse<Course>> {
        return this.http.put<Course>(this.resourceUrl, course, { observe: 'response' });
    }

    query(req?: any): Observable<HttpResponse<Course[]>> {
        const options = createRequestOption(req);
        return this.http.get<Course[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }


    find(id: number): Observable<HttpResponse<Course>> {
        return this.http.get<Course>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }


    getAll(): Observable<HttpResponse<User>> {
        return this.http.get<User>(`${this.resourceUrl}/`, { observe: 'response' });
    }

}
