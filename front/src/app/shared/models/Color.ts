import {EnumerableEntity} from "./EnumerableEntity";

export interface Color extends EnumerableEntity {
    id: number;
    name: string;
    description: string;
}