import {Origin} from './Origin';
import {EnumerableEntity} from "./EnumerableEntity";

export interface Producer extends EnumerableEntity {
    id: number;
    name: string;
    origin: Origin;
}