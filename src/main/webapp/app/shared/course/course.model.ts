import {User} from "../index";

export class Course {
    constructor(
        public id: number,
        public name: string,
        public students: User[]
    ) { }
}
