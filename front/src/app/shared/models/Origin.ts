import {EnumerableEntity} from "./EnumerableEntity";

export interface Origin extends EnumerableEntity {

    id: number;
    name: string;
    shortName: string;
    flag: string;
}